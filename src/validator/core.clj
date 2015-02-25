(ns validator.core)

(defn validate
  "validates a resource"
  [resource rs]
  (map first (remove #((second %1) resource) (rs resource))))


(defn supressed [r] #{})

(defn ruleset [& rules]
  (fn [r]
    (into {}
          (map
           (fn [rule] [(:name rule) (:validator rule)])
           (filter
            #(and
              ((:applies %1) r)
              (not ((supressed r) (:name %1))))
              rules)))))

(defn rule [name & args]
  (merge {:name name :applies (constantly true)}
         (apply hash-map  args)))

