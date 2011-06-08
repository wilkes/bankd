(ns bankd.reports
  (:use [bankd.reporting :only [update-report find-report-by-id find-all]]
        [bankd.event-bus :only [add-subscriber]]))

(defrecord ClientReport [])
(defrecord ClientDetailsReport [])

(defn create-client-report [event]
  (update-report ClientReport event :name :city))

(defn update-client-name [event]
  (update-report ClientReport event :name))

(defn create-client-details-report [event]
  (update-report ClientDetailsReport event
                 :name :street :postal-code
                 :city :phone-number))

(defn update-client-details-name [event]
  (update-report ClientDetailsReport event :name))

(defn show-client [id]
  (find-report-by-id ClientDetailsReport id))

(defn list-clients []
  (find-all ClientReport))

(defn set-subscriptions []
  (add-subscriber 'client-created create-client-report)
  (add-subscriber 'changed-name update-client-name)
  (add-subscriber 'client-created create-client-details-report)
  (add-subscriber 'changed-name update-client-details-name))
