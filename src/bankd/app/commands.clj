(ns bankd.app.commands
  (:use [bankd.support.event-storage :only [fetch-by-id]])
  (:require [bankd.app.domain :as dom])
  (:import [bankd.app.domain Client]))

(defn create-client [params]
  (dom/create-client params))

(defn name-client [id version name]
  (dom/change-name (fetch-by-id Client id)
                   version
                   name))

