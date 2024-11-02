
# Test plan

## List of classes

* List below all classes in your implementation that should have unit tests.
* For each class, list methods that can be tested in isolation.
* For each class, if there are conditions on the class' behaviour that cannot
  be tested by calling one method in isolation, give at least one example of
  a test for such a condition.

It is ok to omit trivial methods (for example, setters and getters), and
classes that have only trivial methods.

- Class Ability
  - class Reroll
    - useAbility
  - class Nox
    - useAbility
  - class UseAgain
    - useAbility
  - class OneX
    - useAbility
  - class ColorChange
    - useAbility
- Class AbilityTrack
  - useAbility
    - everytime useAbility() is called, `abilityAvailable` should decrease by 1 or an error is thrown.
  - useBonus
    - every time useBonus() is called, `bonusAvailable` should decrease by 1 or an error is thrown.
- Class Dice
  - getPossibleTiles
    - should test if it can return all the possible tiles given an dice result
- Class FacadeTile
  - setBrick
    - check if one brick is set into the tile’s shape array
  - getRotatedShape
    - check if the shape is rotated given the required orientation
- Class Game
  - registerPlayer
    - check if the required number of players is initialized and that trackAdvanceStep is set to 2 when the number of players is 2.
- Class Grid
  - getGridScore
    - check if it gives the correct score under current grid state.
  - updateGrid
    - check if the new tile is successfully placed onto the grid
  - canBePlaced
    - Check if it gives the correct decision on whether or not the tile can be placed considering its current state.
- Class Player
  - canUseTile
    - check if all infinite tiles and non-crossed out xtiles can be used.
  - canPlaceTile
  - placeTile