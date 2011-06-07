(ns bankd.test.services
  (:use bankd.services :reload
        [bankd.env :only [reset-env!]]
        midje.sweet))

(background
 (before :contents (reset-env!)))

(fact "name-client increments the version of the client"
  (let [id (:id (create-client {:name .old-name.}))]
    (name-client {:id id :version 1 :name .new-name.})
    => (contains {:id id :version 2 :name .new-name.})))
