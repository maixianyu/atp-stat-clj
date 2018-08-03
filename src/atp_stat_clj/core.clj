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

; item-name eg. "order-id"
(defn grep-item [stat-seq item-name item-map pred]
  (filter
    #(pred (get % (get item-map item-name)))
    stat-seq))

; key-item eg. "play-name"
(defn gen-map [stat-seq key-item item-map pred]
  (loop [inner-seq stat-seq
         res-map (into sorted-map {"R" 1})]
    (if (empty? inner-seq)
      res-map
      (do
        (let [key-ele (get (first inner-seq) (get item-map key-item))]
          (assoc res-map
            {key-ele (+ (get res-map key-ele 0) (pred (first inner-seq)))}))
        (recur (next inner-seq)
               res-map)))))

(gen-map all-stats "winner_name" item-map #(if (empty? %) 1 0))

(assoc (sorted-map) "nobody" 1)

(get item-map "winner_name")


(def win_count_roger
  (count (grep-item all-stats
             "winner_name"
             item-map
             #(str/includes? % "Roger Federer"))))


