(ns bankd.event-bus
  (require [lamina.core :as lamina]))

(defprotocol EventBus
  (publish [this event])
  (subscribe [this event-name handler]))

(extend-type lamina.core.channel.Channel
  EventBus
  (publish [ch event]
    (lamina/enqueue ch event))
  (subscribe [ch event-name handler]
    (lamina/receive-all (lamina/filter* #(= (:name %) (-> event-name name symbol))
                                        ch)
                        handler)))

(defn in-process-bus []
  (lamina/channel))
