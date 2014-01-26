(ns bier.core
  (:require [bier.malt :as m])
  (:require [bier.hops :as h])
  (:require [bier.kettle :as k])
  (:gen-class))

; using http://morebeer.com/brewingtechniques/library/backissues/issue2.1/manning.html
; estimate yields http://www.howtobrew.com/section2/chapter12-4-1.html

; Define your beer up here
(def bier { :og 1.09 :ibu 50 :l 10 :batch 5.25 })

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
(m/set-efficiency 0.85)
(def total-malt-weight (m/total-weight (:og bier) (:batch bier) malts))
(def malt-weights
  (let [weights
        (vec (map #(m/weigh % total-malt-weight) malts))]
    (apply str (for [idx (range 0 (count weights))]
      (format "%16s: %5.2f lbs\n"
        (:name (malts idx)) (weights idx))))))

(def predicted-colour
  (m/predicted-colour total-malt-weight (:batch bier) malts))

; Hops funcs
(def total-hops-weight (h/total-weight (:ibu bier) (:batch bier) hops))
(def hop-weights
  (let [weights
        (vec (map #(h/weigh % total-hops-weight) hops))]
    (apply str (for [idx (range 0 (count weights))]
      (format "%16s: %4.2f oz\n"
        (:name (hops idx)) (weights idx))))))

; Volume equations

(def kettle-volume (k/volume evap-rate boil-time (:batch bier)))
(def mash-water (k/mash-water quarts-per-pound total-malt-weight))
(def sparge-water (k/sparge-water kettle-volume mash-water total-malt-weight))
(def sparge-to-mash (k/ratio sparge-water mash-water))


(defn print-divider
  "" []
  (println "================================================="))
(defn print-underline
  "" []
  (println "-------------------------------------------------"))

(defn print-bier
  "Pretty print the beer profile"
  []
  (println "Desired Beer Profile:")
  (print-divider)
  (printf "  OG:         %1.2f\n" (:og bier))
  (printf "  IBU:        %4d\n" (:ibu bier))
  (printf "  Colour:     %4d L\n" (:l bier))
  (printf "  Batch Size: %2.2f gal\n\n" (:batch bier))
  )

(defn print-recipe
  "pretty-print the recipe"
  []
  (println "Recipe:")
  (print-divider)
  (printf "Total Weight: %5.2f\n\n" total-malt-weight)
  (println "Malt Weights:")
  (print-underline)
  (print malt-weights)
  (print "\n")
  (printf "Predicted colour: %3.0f L\n\n" predicted-colour)
  (println "Hops Weights:")
  (print-underline)
  (print hop-weights)
  (print "\n")
  (println "Volumes:")
  (print-underline)
  (printf " Kettle Volume: %2.2f gal\n" kettle-volume)
  (printf "    Mash Water: %2.2f gal\n" mash-water)
  (printf "  Sparge Water: %2.2f gal\n" sparge-water)
  (printf "         Ratio: %1.2f (aim for 1.5)\n" sparge-to-mash)
  (printf "Boil time: %1.2f hours\n" boil-time))

(defn -main
  ""
  [& args]
  (print-bier)
  (print-recipe))
