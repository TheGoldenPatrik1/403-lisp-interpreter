(print (or? truth truth))
(print (or? truth nil))
(print (or? nil truth))
(print (or? nil nil))

(print (or? (= 1 1) (= 1 2)))
(print (or? (= 1 2) (= 1 2)))

(print (or? (= 1 1) (+ 1)))
(print (or? (= 1 2) (+ 1)))