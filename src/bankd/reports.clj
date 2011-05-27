(ns bankd.reports
  (:use [bankd.core :only [update-report]]))

(defrecord ClientDetailsReport [])

(defmulti on :name)

(defmethod on 'client-created
  [event]
  (update-report ClientDetailsReport
                 (select-keys (:data event)
                              [:name :street :postal-code
                               :city :phone-number :uid])))

(defmethod on 'changed-name
  [event]
  (update-report ClientDetailsReport
                 (select-keys (:data event)
                              [:uid :name])))
