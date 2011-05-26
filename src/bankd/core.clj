(ns bankd.core)

(declare *domain-repository*)

(defn commit []
  (doseq [event *domain-repository*]
    (println event)))

(defmacro with-domain-repository [& body]
  `(binding [*domain-repository* []]
     (let [result# ~@body]
       (commit)
       result#)))

(defn execute-command [cmd params]
  (with-domain-repository
    (cmd params)))

(defn apply-event [aggregate event-handler attributes]
  (let [event {:name (:name (meta event-handler))
               :data attributes}
        aggregate (event-handler aggregate event)]
    (conj *domain-repository* (assoc event :aggregate-uid (:uid aggregate)))
    aggregate))



(defrecord Client [])

(defn client-created [this event]
  (merge (:data event)
         (assoc this
           :uid (:uid event)
           :account-uids []
           :card-numbers [])))

(defn make-client [attributes]
  (apply-event (Client.) client-created attributes))

(defn create-client-cmd [params]
  (make-client params))

(defn create-client [params]
  (execute-command create-client-cmd params))
