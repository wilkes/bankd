(ns bankd.app.services
  (:use [bankd.support.command-bus :only [execute-command]])
  (:require [bankd.app.commands :as cmd]
            [bankd.app.reports :as reports]))

(defn create-client [params]
  (execute-command cmd/create-client params))

(defn name-client [params]
  (execute-command cmd/name-client (:id params) (:version params) (:name params)))

(defn show-client [params]
  (reports/show-client (:id params)))

(defn list-clients [params]
  (reports/list-clients))
