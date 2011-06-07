(ns bankd.reports
  (:use [bankd.reporting :only [update-report find-report-by-id]]
        [bankd.event-bus :only [add-subscriber]]))

(defrecord ClientDetailsReport [])

(defn create-client-details-report [event]
  (update-report ClientDetailsReport event
                 :name :street :postal-code
                 :city :phone-number))

(defn update-client-details-name [event]
  (update-report ClientDetailsReport event :name))

(defn show-client [id]
  (find-report-by-id ClientDetailsReport id))

(defn set-subscriptions []
  (add-subscriber 'name-changed update-client-details-name)
  (add-subscriber 'client-created create-client-details-report))
