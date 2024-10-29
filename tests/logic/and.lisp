(print (and? truth truth))
(print (and? truth nil))
(print (and? nil truth))
(print (and? nil nil))

(print (and? (> 1 0) (> 2 1)))
(print (and? (< 1 0) (> 2 1)))
(print (and? (> 1 0) (< 2 1)))

(print (and? (not (= 5 3)) (= 5 5)))
(print (and? (not (= 5 3)) (= 5 3)))

(print (and? (= 5 3) (+ 1)))
(print (and? (= 5 5) (+ 1)))