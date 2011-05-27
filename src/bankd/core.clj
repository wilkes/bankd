(ns bankd.core)

(declare *domain-repository*)

(def *event-log* (atom {}))
(def *reports* (atom {}))

(defn find-report [type id]
  (get-in @*reports* [type id]))

(defn uuid [instance]
  (merge {:uid (str (java.util.UUID/randomUUID))} instance))

(defn create-report [type data]
  (let [do-create (fn [reports]
                    (let [type-reports (get reports type {})]
                      (assoc reports type
                             (assoc type-reports (:uid data) data))))]
    (swap! *reports* do-create)))

(defn update-report [type new-data]
  (let [do-create (fn [reports]
                    (let [id (:uid new-data)
                          type-reports (get reports type {})
                          old-data (type-reports id)
                          new-data (merge old-data new-data)]
                      (assoc reports type
                             (assoc type-reports id new-data))))]
    (swap! *reports* do-create)))

(defn find-report-by-id [type id]
  (get-in @*reports* [type id]))

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

(defn exist [o]
  (not (nil? (:uid o))))

(defn should [f? o]
  (when-not (f? o)
    (let [t (pr-str (type o))
          v (:name (meta f?))]
      (throw (RuntimeException. (str t " should " v "."))))))

(defn fetch-by-id [type-var id]
  (reduce (fn [obj event]
            (println event)
            (let [f (ns-resolve (:ns event) (:name event))]
              (f obj event)))
          ^{:type type-var} {}
          (get @*event-log* id)))
