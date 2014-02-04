(ns clspam.hasher)

(defn- strnhash [s]
  (let [byte-arr (.getBytes s)]
    (loop [hval (count byte-arr)
           chr (first byte-arr)
           left (next byte-arr)]
      (let [tmp (bit-or chr (bit-shift-left chr 8) (bit-shift-left chr 16) (bit-shift-left chr 24))
            a (bit-xor hval tmp)
            b (+ a (bit-and (bit-shift-right a 12) 0x0000ffff))
            tmp2 (bit-or (bit-shift-left b 24) (bit-and (bit-shift-right b 24) 0x000000ff))
            c (bit-or tmp2 (bit-and b 0x00ffff00))
            new-hval (bit-or (bit-shift-left c 3) (bit-and (bit-shift-right c 29) 0x7))]
        (if left
          (recur new-hval (first left) (next left))
          new-hval)))))

(defn hasher [coeff tokens]
  "Hashes a token sequence.
  Hashes are made by multiplying by the coeff 2d vector.
  Each row in coeff will return one hash."
  (let [pipe (atom (map (fn [_] identity 0xdeadbeef) (range (apply max (map count coeff)))))]
    (flatten (map
              (fn [token]
                (swap! pipe (comp butlast conj) (strnhash token))
                (let [curpipe @pipe]
                  (map #(apply +' (map *' curpipe %)) coeff)))
              tokens))))
