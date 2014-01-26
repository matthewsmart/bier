(ns bier.kettle)

(defn volume
  "Calculate preboil volume"
  [evap-rate boil-time batch-size]
  (let [water-evaporated (* evap-rate boil-time)]
    (+ batch-size water-evaporated)))

(defn mash-water
  "Calculate the mash water"
  [quarts-per-pound malt-weight]
  (let [gal-per-pound (/ quarts-per-pound 4)]
    (* gal-per-pound malt-weight)))

(defn sparge-water
  "Calculate the amount of sparge water"
  [kettle-volume mash-water malt-weight]
  (let [water-absorbed (* 0.1 malt-weight)]
    (+ (- kettle-volume mash-water) water-absorbed)))

(defn ratio
  "Calculate the sparge to mash ratio (aim for 1.5)"
  [sparge-water mash-water]
  (/ sparge-water mash-water))
