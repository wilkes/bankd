(ns bankd.env
  (:use [bankd.event-bus :only [in-process-bus]]
        [bankd.event-storage :only [*event-log*]]
        [bankd.reports :only [set-subscriptions]]))

(defn reset-env! []
  (swap! *event-log* (constantly {}))
  (in-process-bus)
  (set-subscriptions))
