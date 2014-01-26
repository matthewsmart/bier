(ns bier.malt)

(def efficiency (atom 0))

(defn- calc-extracts
  "Calculates the extract from the malt"
  [malt]
  (* (:percent malt) (:py malt) @efficiency))

(defn set-efficiency
  "Set the efficiency of the mash"
  [estimated-efficiency]
  (reset! efficiency estimated-efficiency))

(defn total-weight
  "Obtain the total weight of grain to use"
  [original-gravity batch-size malts]
  (let [total-extract
        (let [extracts (map calc-extracts malts)] (reduce + extracts))]
    (/ (* (+ original-gravity -1.) 1000 batch-size)
       (* total-extract 46.31))))

(defn weigh
  "Obtain the weight of an individual malt"
  [malt total-weight]
  (* (:percent malt) total-weight))

(defn- calc-colours
  "Calculates the colour from the malt"
  [malt]
  (* (:percent malt) (:l malt)))

(defn predicted-colour
  "Generated the predicted colour of the beer"
  [total-weight batch-size malts]
  (let [total-colour (reduce + (map calc-colours malts))]
    (/ (* total-colour total-weight) batch-size)))

