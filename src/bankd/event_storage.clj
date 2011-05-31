(ns bankd.event-storage)

(def *event-log* (atom {}))

(defn commit [event]
  (swap! *event-log*
         (fn [el]
           (let [events (get el (:aggregate-uid event) [])]
             (assoc el (:aggregate-uid event)
                    (conj events event)))))
  (println @*event-log*))

(defn fetch-by-id [type-var id]
  (reduce (fn [obj event]
            (println event)
            (let [f (ns-resolve (:ns event) (:name event))]
              (f obj event)))
          ^{:type type-var} {}
          (get @*event-log* id)))
