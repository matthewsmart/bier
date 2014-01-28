(ns bier.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

; using http://morebeer.com/brewingtechniques/library/backissues/issue2.1/manning.html
; estimate yields http://www.howtobrew.com/section2/chapter12-4-1.html

(def cli-options
  [["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Turn a list of ingredients into a full blown beer recipe"
        ""
        "Usage: bier path-to-recipe.json"
        ""
        "Options:"
        options-summary]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (let [path-to-recipe (first arguments)]
      (let [beer (bier.Recipe. path-to-recipe)]
        (.print beer)))))
