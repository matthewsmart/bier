(ns bier.Recipe
  (:gen-class
    :state        state
    :prefix       "-"
    :init         init
    :main         false
    :constructors {[String] []}
    :methods [[beer [] clojure.lang.PersistentArrayMap]
              [malts [] clojure.lang.PersistentVector]
              [hops [] clojure.lang.PersistentVector]
              [mash [] clojure.lang.PersistentArrayMap]
              [totalMaltWeight [] Double]
              [maltWeightsString [] String]
              [predictedColour [] Double]
              [totalHopsWeight [] Double]
              [hopsWeightsString [] String]
              [kettleVolume [] Double]
              [mashWater [] Double]
              [spargeWater [] Double]
              [spargeToMash [] Double]
              [print [] String]])
  (:require [clojure.data.json :as json]
            [bier.malt :as m]
            [bier.hops :as h]
            [bier.kettle :as k]))

(defn -init
  [path-to-recipe]
  [[] (atom {:recipe (json/read-str (slurp path-to-recipe) :key-fn keyword)})])

(defn -beer
  [this]
  (:beer (:recipe @(.state this))))

(defn -malts
  [this]
  (:malts (:recipe @(.state this))))

(defn -hops
  [this]
  (:hops (:recipe @(.state this))))

(defn -mash
  [this]
  (:mash (:recipe @(.state this))))

(defn -totalMaltWeight
  [this]
  (:total-malt-weight @(.state this)))

(defn -maltWeightsString
  [this]
  (:malt-weights-string @(.state this)))

(defn -predictedColour
  [this]
  (:predicted-colour @(.state this)))

(defn -totalHopsWeight
  [this]
  (:total-hops-weight @(.state this)))

(defn -hopsWeightsString
  [this]
  (:hops-weights-string @(.state this)))

(defn -kettleVolume
  [this]
  (:kettle-volume @(.state this)))

(defn -mashWater
  [this]
  (:mash-water @(.state this)))

(defn -spargeWater
  [this]
  (:sparge-water @(.state this)))

(defn -spargeToMash
  [this]
  (:sparge-to-mash @(.state this)))

(defn total-malt-weights
  [recipe]
  (let [og         (:og (.beer recipe))
        batch-size (:batch (.beer recipe))
        efficiency (:estimated-efficiency (.mash recipe))
        malts      (.malts recipe)]
    (m/set-efficiency efficiency)
    (let [total-malt-weight (m/total-weight og batch-size malts)]
    (swap! (.state recipe) assoc
           :total-malt-weight total-malt-weight
           :malt-weights-string
            (let [weights (vec (map #(m/weigh % total-malt-weight) malts))]
              (apply str (for [idx (range 0 (count weights))]
                           (format "%16s: %5.2f lbs\n"
                                   (:name (malts idx)) (weights idx)))))))))

(defn predict-colour
  [recipe]
  (let [total-malt-weight (.totalMaltWeight recipe)
        batch-size        (:batch (.beer recipe))
        malts             (.malts recipe)]
    (swap! (.state recipe) assoc
           :predicted-colour (m/predicted-colour total-malt-weight batch-size malts))))

(defn total-hops-weights
  [recipe]
  (let [batch-size (:batch (.beer recipe))
        ibu        (:ibu (.beer recipe))
        hops       (.hops recipe)]
    (let [total-hops-weight (h/total-weight ibu batch-size hops)]
      (swap! (.state recipe) assoc
             :total-hops-weight total-hops-weight
             :hops-weights-string
             (let [weights (vec (map #(h/weigh % total-hops-weight) hops))]
               (apply str (for [idx (range 0 (count weights))]
                            (format "%16s: %4.2f oz\n" (:name (hops idx)) (weights idx)))))))))

(defn calc-water-volumes
  [recipe]
  (let [evap-rate (:evap-rate (.mash recipe))
        boil-time (:boil-time (.mash recipe))
        batch-size (:batch (.beer recipe))
        quarts-per-pound (:quarts-per-pound (.mash recipe))
        total-malt-weight (.totalMaltWeight recipe)]
    (let [kettle-volume (k/volume evap-rate boil-time batch-size)
          mash-water (k/mash-water quarts-per-pound total-malt-weight)]
      (let [sparge-water (k/sparge-water kettle-volume mash-water total-malt-weight)]
        (let [sparge-to-mash (k/ratio sparge-water mash-water)]
          (swap! (.state recipe) assoc
                 :kettle-volume kettle-volume
                 :mash-water mash-water
                 :sparge-water sparge-water
                 :sparge-to-mash sparge-to-mash))))))

(defn print-divider
  "" []
  (println "================================================="))

(defn print-underline
  "" []
  (println "-------------------------------------------------"))

(defn print-bier
  "Pretty print the beer profile"
  [recipe]
  (println "Desired Beer Profile:")
  (print-divider)
  (printf "  OG:         %1.2f\n" (:og (.beer recipe)))
  (printf "  IBU:        %4d\n" (:ibu (.beer recipe)))
  (printf "  Colour:     %4d L\n" (:l (.beer recipe)))
  (printf "  Batch Size: %2.2f gal\n\n" (:batch (.beer recipe)))
  )

(defn print-recipe
  "pretty-print the recipe"
  [recipe]
  (println "Recipe:")
  (print-divider)
  (printf "Total Weight: %5.2f\n\n" (.totalMaltWeight recipe))
  (println "Malt Weights:")
  (print-underline)
  (print (.maltWeightsString recipe))
  (print "\n")
  (printf "Predicted colour: %3.0f L\n\n" (.predictedColour recipe))
  (println "Hops Weights:")
  (print-underline)
  (print (.hopsWeightsString recipe))
  (print "\n")
  (println "Volumes:")
  (print-underline)
  (printf " Kettle Volume: %2.2f gal\n" (.kettleVolume recipe))
  (printf "    Mash Water: %2.2f gal\n" (.mashWater recipe))
  (printf "  Sparge Water: %2.2f gal\n" (.spargeWater recipe))
  (printf "         Ratio: %1.2f (aim for 1.5)\n" (.spargeToMash recipe))
  (printf "Boil time: %1.2f hours\n" (:boil-time (.mash recipe))))

(defn compile-recipe
  [recipe]
  (total-malt-weights recipe)
  (predict-colour recipe)
  (total-hops-weights recipe)
  (calc-water-volumes recipe))

(defn -print
  [this]
  (compile-recipe this)
  (print-bier this)
  (print-recipe this))
