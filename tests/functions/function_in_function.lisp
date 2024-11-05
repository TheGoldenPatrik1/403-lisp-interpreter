(define double (x) (* 2 x))
(define quadruple (x) (+ (double x) (double x)))
(print (quadruple 1))