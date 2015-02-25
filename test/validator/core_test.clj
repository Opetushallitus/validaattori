(ns validator.core-test
  (:use midje.sweet
        validator.core))



(fact (validate :valid-resource ...ruleset...) => []
      (provided
       (...ruleset... :valid-resource) => {:rule1 (constantly true)
                                           :rule2 (constantly true)}))

(fact (validate :invalid-resource ...ruleset...) => [:rule1 :rule2]
      (provided
       (...ruleset... :invalid-resource) => {:rule1 (constantly false)
                                            :rule2 (constantly false)}))


(fact (ruleset
         ...rule...) => fn?)

(fact ((ruleset
         (rule :name
             :validator ...predicate...)) :resource) => {:name ...predicate...})

(fact ((ruleset
        (rule :rule1
              :applies #{:resource}
              :validator ...predicate1...)
        (rule :rule2
              :applies #{:resource}
              :validator ...predicate2...)) :resource) => {:rule1 ...predicate1...
                                                           :rule2 ...predicate2...})


(fact ((ruleset
        (rule :rule1
              :applies #{:resource}
              :validator ...predicate1...)
        (rule :rule2
              :applies #{:resource2}
              :validator ...predicate2...)) :resource) => {:rule1 ...predicate1...})


(fact ((ruleset
        (rule :rule1
              :applies #{:resource}
              :validator ...predicate1...)
        (rule :rule2
              :applies #{:resource}
              :validator ...predicate2...)) :resource) => {:rule1 ...predicate1...}
      (provided
       (supressed :resource) => #{:rule2}))


