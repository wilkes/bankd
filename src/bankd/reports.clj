(ns bankd.reports
  (:use [bankd.reporting :only [create-report update-report]]
        [bankd.event-bus :only [add-subscriber]]))

(defrecord ClientDetailsReport [])

(defn create-client-details-report [event]
  (create-report ClientDetailsReport
                 (merge {:uid (:aggregate-uid event)}
                        (select-keys (:data event)
                                     [:name :street :postal-code
                                      :city :phone-number]))))
(add-subscriber 'client-created create-client-details-report)

(defn update-client-details-name [event]
  (update-report ClientDetailsReport
                 (merge {:uid (:aggregate-uid event)}
                         (select-keys (:data event)
                                      [:uid :name]))))
(add-subscriber 'name-changed update-client-details-name)
