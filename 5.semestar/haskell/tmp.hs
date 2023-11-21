import Data.Char (isUpper)

applyAll :: a -> [a -> b] -> [b]
applyAll val = map $ \f -> f val

sumEven :: (Num a) => [a] -> a
sumEven xs = sum $ map snd $ filter (even . fst) $ zip [0, 1 ..] xs

applyTuples :: [((a -> b), a)] -> [b]
applyTuples = map (uncurry ($))

isTitleCased :: String -> Bool
isTitleCased = all (isUpper . head) . words

sortPairs xs = uncurry sort xs