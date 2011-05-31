(ns bankd.reporting)

(def *reports* (atom {}))

(defn create-report [type data]
  (let [do-create (fn [reports]
                    (let [type-reports (get reports type {})]
                      (assoc reports type
                             (assoc type-reports (:uid data) data))))]
    (swap! *reports* do-create)
    (println "Reports: " @*reports*)))

(defn update-report [type new-data]
  (let [do-create (fn [reports]
                    (let [id (:uid new-data)
                          type-reports (get reports type {})
                          old-data (type-reports id)
                          new-data (merge old-data new-data)]
                      (assoc reports type
                             (assoc type-reports id new-data))))]
    (swap! *reports* do-create)
    (println "Reports: " @*reports*)))

(defn find-report-by-id [type id]
  (get-in @*reports* [type id]))
