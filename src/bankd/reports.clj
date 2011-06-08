(ns bankd.reports
  (:use [bankd.reporting :only [update-report find-report-by-id find-all]]
        [bankd.event-bus :only [add-subscriber]]))

(defrecord ClientReport [])
(defrecord ClientDetailsReport [])

(add-subscriber 'client-created
                #(update-report ClientReport % :name :city))

(add-subscriber 'changed-name
                #(update-report ClientReport % :name))

(add-subscriber 'client-created
                #(update-report ClientDetailsReport %
                                :name :street :postal-code
                                :city :phone-number))

(add-subscriber 'changed-name
                #(update-report ClientDetailsReport % :name))

(defn show-client [id]
  (find-report-by-id ClientDetailsReport id))

(defn list-clients []
  (find-all ClientReport))

