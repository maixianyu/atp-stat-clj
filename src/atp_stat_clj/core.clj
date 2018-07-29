(ns atp-stat-clj.core
  (:require [clojure-csv.core :as csv])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

; config file path
(def dir "resources/csv/2_match_scores/")
(def file-list "file.list")
(def item-map-file "match_scores_column_titles.txt")

(def item-map
  (zipmap
    (line-seq (io/reader (str dir item-map-file)))
    (range (count (line-seq (io/reader (str dir item-map-file)))))
    ))

item-map

(defn file-path-list [dir-path file-list]
  (map #(str dir-path %)
    (line-seq
      (io/reader (str dir-path file-list)))))

; func to read a csv file
(defn read-csvfile [s]
  (csv/parse-csv (io/reader s)))

; func to read many csv file
(defn read-and-merge-csv-file [dir-path file-list]
  (reduce
    concat
    (map read-csvfile (file-path-list dir-path file-list))))

; get all-stats
(def all-stats (read-and-merge-csv-file dir file-list))

(count all-stats)

(defn grep-item [stat-seq item-name item-map pred]
  (filter
    #(pred (get % (get item-map item-name)))
    stat-seq))

(get item-map "winner_name")


(def win_count_roger
  (count (grep-item all-stats
             "winner_name"
             item-map
             #(str/includes? % "Roger Federer"))))

(def lose_count_roger
  (count (grep-item all-stats
             "loser_name"
             item-map
             #(str/includes? % "Roger Federer"))))

(def win_count_dj
  (count (grep-item all-stats
             "winner_name"
             item-map
             #(str/includes? % "Novak Djokovic"))))

(def lose_count_dj
  (count (grep-item all-stats
             "loser_name"
             item-map
             #(str/includes? % "Novak Djokovic"))))

; 81.9%
(/ win_count_roger (+ win_count_roger lose_count_roger))
win_count_dj
lose_count_dj

; 83.2%
(/ win_count_dj (+ lose_count_dj win_count_dj))
