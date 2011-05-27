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
                     :ns (:ns (meta event-handler))
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
            (let [f (ns-resolve (:ns event) (:name event))]
              (f obj event)))
          ^{:type type-var} {}
          (get @*event-log* id)))
