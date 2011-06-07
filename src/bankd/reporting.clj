(ns bankd.reporting)

(def *reports* (atom {}))

(defn update-report [type event & keys]
  (let [data (merge {:id (:aggregate-id event)
                     :version (:version event)}
                    (select-keys (:data event) keys))]
    (swap! *reports*
           #(update-in % [(pr-str type) (:id data)] merge data))))

(defn find-report-by-id [type id]
  (get-in @*reports* [(pr-str type) id]))
