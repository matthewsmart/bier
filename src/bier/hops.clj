(ns bier.hops)

(defn- calc-acid
  "Calculate the iso acid of an individual ingredient"
  [hop]
  (* (:percent hop) (:alpha-acid hop) (:utilization hop) 7490))

(defn total-weight
  "Calculate the total weight of hops to be used"
  [ibu batch-size hops]
  (let [total-acids (reduce + (map calc-acid hops))]
    (/ (* ibu batch-size) total-acids)))

(defn weigh
  "Calculate the weight of each hops"
  [hop total-weight]
  (* (:percent hop) total-weight))

