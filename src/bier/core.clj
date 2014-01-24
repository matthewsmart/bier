(ns bier.core
  (:gen-class))

; using http://morebeer.com/brewingtechniques/library/backissues/issue2.1/manning.html
; estimate yields http://www.howtobrew.com/section2/chapter12-4-1.html

(def bier (hash-map :og 1.09 :ibu 50 :l 10 :batch 5.25))
(def eff 0.85)
;                  %W   Ing.               EY   L
(def malts (list '(0.8  "Ashburne Mild"    1.   5.3)
                 '(0.05 "Black Malt"       0.55 474.)
                 '(0.05 "Crystal 120"      0.72 120.)
                 '(0.05 "Honey Malt"       0.75 22.5)
                 '(0.05 "Munich Malt Dark" 0.75 9.)))

(def hops (list '(1. "amarillo" 0.082 60. 0.25)))

(defn calc-extracts
  "Calculates the extract from the malt"
  [malt]
  (* (nth malt 0) (nth malt 2) eff))

(def extracts (map calc-extracts malts))

(defn calc-colours
  "Calculates the colour from the malt"
  [malt]
  (* (nth malt 0) (nth malt 3)))

(def colours (map calc-colours malts))

(def total-extract (reduce + extracts))

(def total-weight
  (/ (* (+ (get bier :og) -1.) 1000 (get bier :batch))
   (* total-extract 46.31)))

(defn weigh-malt
  "Use total weight to calculate ingredient weight"
  [malt]
  (* (nth malt 0) total-weight))

(def ingredient-weights (map weigh-malt malts))

(defn render-weights
  "Prints the weights of the malts"
  []
  (for [idx (range 0 (count ingredient-weights))]
    (str "  " (nth (nth malts idx) 1) ": " (nth ingredient-weights idx) "lbs\n")))

(def total-colour
  (reduce + colours))
(def predicted-colour (/ (* total-colour total-weight) (get bier :batch)))

(defn calc-iso-a-acid
  ""
  [hop]
  (* (nth hop 0) (nth hop 2) (nth hop 4) 7490))

(def iso-a-acids (map calc-iso-a-acid hops))
(def total-iso-a-acid (reduce + iso-a-acids))

(def total-hop-weight (/ (* (get bier :ibu) (get bier :batch)) total-iso-a-acid))
(eval total-hop-weight)

(defn -main
  ""
  [& args]
  (println bier)
  (println (str "Total Weight: " total-weight))
  (println "Malt Weights:")
  (apply print (render-weights))
  (println (str "Predicted colour: " predicted-colour))
  (println (str "Total Hop Weight: " total-hop-weight "oz")))
