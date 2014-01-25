(ns bier.core
  (:gen-class))

; using http://morebeer.com/brewingtechniques/library/backissues/issue2.1/manning.html
; estimate yields http://www.howtobrew.com/section2/chapter12-4-1.html

; Define your beer up here
(def bier { :og 1.09 :ibu 50 :l 10 :batch 5.25 })
(def eff 0.85)

(def malts [{ :percent 0.8  :name "Ashburne Mild"    :py 1.   :l 5.3  }
            { :percent 0.05 :name "Black Malt"       :py 0.55 :l 474. }
            { :percent 0.05 :name "Crystal 120"      :py 0.72 :l 120. }
            { :percent 0.05 :name "Honey Malt"       :py 0.75 :l 22.5 }
            { :percent 0.05 :name "Munich Malt Dark" :py 0.75 :l 9.   }])

(def hops [{ :percent 1. :name "Amarillo" :alpha-acid 0.082 :boil-time 60. :utilization 0.25}])

(def evap-rate 1.15) ; gal/hour
(def boil-time 1.25) ; hours
(def quarts-per-pound 1.)

; Malting functions
(defn calc-extracts
  "Calculates the extract from the malt"
  [malt]
  (* (:percent malt) (:py malt) eff))

(def total-weight
  (let [total-extract
        (let [extracts (map calc-extracts malts)] (reduce + extracts))]
    (/ (* (+ (:og bier) -1.) 1000 (:batch bier))
       (* total-extract 46.31))))

(defn weigh-malt
  "Use total weight to calculate ingredient weight"
  [malt]
  (* (:percent malt) total-weight))

(def malt-weights
  (let [ingredient-weights (vec (map weigh-malt malts))]
    (apply str (for [idx (range 0 (count ingredient-weights))]
                 (format "%16s: %5.2f lbs\n"  (:name (malts idx)) (ingredient-weights idx))))))

; Colour functions
(defn calc-colours
  "Calculates the colour from the malt"
  [malt]
  (* (:percent malt) (:l malt)))

(def predicted-colour
  (let [total-colour (reduce + (map calc-colours malts))]
    (/ (* total-colour total-weight) (:batch bier))))

; Hops funcs
(defn calc-iso-acid
  "Calculate the iso acid of an individual ingredient"
  [hop]
  (* (:percent hop) (:alpha-acid hop) (:utilization hop) 7490))

(def total-hop-weight
  (let [total-iso-acid (reduce + (map calc-iso-acid hops))]
    (/ (* (:ibu bier) (:batch bier)) total-iso-acid)))

(defn weigh-hops
  "Calculate the weight of each hops"
  [hop]
  (* (:percent hop) total-hop-weight))

(def hop-weights
  (let [vec-hop-weights (vec (map weigh-hops hops))]
    (apply str (for [idx (range 0 (count vec-hop-weights))]
                 (format "%16s: %4.2f oz\n" (:name (hops idx)) (vec-hop-weights idx))))))

; Volume equations

(def kettle-volume
  (let [water-evaporated (* evap-rate boil-time)]
    (+ (:batch bier) water-evaporated)))

(def mash-water
  (let [gal-per-pound (/ quarts-per-pound 4)]
    (* gal-per-pound total-weight)))

(def sparge-water
  (let [water-absorbed (* 0.1 total-weight)]
    (+ (- kettle-volume mash-water) water-absorbed)))

(def sparge-to-mash (/ sparge-water mash-water))

(defn -main
  ""
  [& args]
  (println bier)
  (printf "Total Weight: %5.2f\n" total-weight)
  (println "Malt Weights:")
  (print malt-weights)
  (printf "Predicted colour: %3.0f L\n" predicted-colour)
  (println "Hops Weights:")
  (print hop-weights)
  (println "Volumes:")
  (printf " Kettle Volume: %2.2f gal\n" kettle-volume)
  (printf "    Mash Water: %2.2f gal\n" mash-water)
  (printf "  Sparge Water: %2.2f gal\n" sparge-water)
  (printf "         Ratio: %1.2f (aim for 1.5)\n" sparge-to-mash)
  (printf "Boil time: %1.2f hours\n" boil-time)
  )
