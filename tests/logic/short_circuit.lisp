(define test (x) (print x))
(print (and? (test 1) (test 1)))

(print (and? nil (test 1)))

(print (or? truth (test 1)))