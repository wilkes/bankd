(ns bankd.test.services
  (:use bankd.app.services
        [bankd.support.env :only [reset-env!]]
        midje.sweet))

(background
 (around :facts (do (reset-env!)
                    ?form)))

(against-background
  [(around :facts (let [id (:id (create-client {:name .name.}))]
                    ?form))]

 (fact "name-client increments the version of the client"
   (name-client {:id id :version 1 :name .new-name.})
   => (contains {:id id :version 2 :name .new-name.}))

 (fact "show client"
   (show-client {:id id}) => (contains {:id id :version 1 :name .name.}))

 (fact "show updated client"
   (name-client {:id id :version 1 :name .new-name.})
   (show-client {:id id}) => (contains {:id id :version 2 :name .new-name.}))

 (fact "list clients"
   (count (list-clients nil)) => 1
   (create-client {:name .another.})
   (count (list-clients nil)) => 2
   (name-client {:id id :version 1 :name .new-name.})
   (count (list-clients nil)) => 2
   (list-clients nil) => (contains [(contains {:version 1 :name .another.})
                                    (contains {:version 2 :name .new-name.})] :in-any-order)))
