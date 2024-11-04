(define max-of-three (a b c)
  (if (> a b)
      (if (> a c)
          a
          c)
      (if (> b c)
          b
          c)))
(print (max-of-three 10 15 5))