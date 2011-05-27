(ns bankd.core)

(declare *domain-repository*)

(def *event-log* (atom {}))

(defn uuid [instance]
  (merge {:uid (str (java.util.UUID/randomUUID))} instance))

(defn commit []
  (doseq [event @*domain-repository*]
    (swap! *event-log*
           (fn [el]
             (let [events (get el (:aggregate-uid event) [])]
               (assoc el (:aggregate-uid event)
                      (conj events event))))))
  (println @*event-log*))

(defmacro with-domain-repository [& body]
  `(binding [*domain-repository* (atom [])]
     (let [result# ~@body]
       (commit)
       result#)))

(defn execute-command [cmd & args]
  (with-domain-repository
    (apply cmd args)))

(defn apply-event [aggregate event-handler attributes]
  (let [event (uuid {:name (:name (meta event-handler))
                    :data attributes})
        aggregate (event-handler aggregate event)]
    (swap! *domain-repository* conj (assoc event :aggregate-uid (:uid aggregate)))
    aggregate))

(defn should-exist [o]
  (when (nil? (:uid o))
    (throw (RuntimeException. "Object does not exist"))))

(defn fetch-by-id [type-var id]
  (reduce (fn [obj event]
            (println event)
            (let [f (resolve (:name event))]
              (f obj event)))
          ^{:type type-var} {}
          (get @*event-log* id)))

(defrecord Client [])

(defn client-created [this event]
  (merge (:data event)
         (assoc this
           :uid (:uid event)
           :account-uids []
           :card-numbers [])))

(defn do-create-client []
  (apply-event (Client.) client-created nil))

(defn create-client-cmd []
  (do-create-client))

(defn create-client [params]
  (execute-command create-client-cmd))

(defn name-changed [this event]
  (assoc this :name (-> event :data :name)))

(defn change-name [client name]
  (should-exist client)
  (apply-event client name-changed {:client-uid (:uid client)
                                    :name name})  )
(defn name-client-cmd [id name]
  (change-name (fetch-by-id Client id) name))

(defn name-client [params]
  (execute-command name-client-cmd (:id params) (:name params)))
