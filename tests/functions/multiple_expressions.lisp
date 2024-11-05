(define doubleprint (x) (print x) (print x))
(print (doubleprint 1))

(define addandprint (x) (set a (+ x 1)) (print a))
(addandprint 1)