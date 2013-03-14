define surveyor position
(
  size [0.13 0.11]


# the shape of Bigbob
polygons 4

# the shape of Bigbob
polygon[0].points 4
polygon[0].point[0] [1 2]
polygon[0].point[1] [1 9]
polygon[0].point[2] [12 9]
polygon[0].point[3] [12 2]


# RIGHT WHEEL
polygon[1].points 4
polygon[1].point[0] [0 2]
polygon[1].point[1] [13 2]
polygon[1].point[2] [13 0]
polygon[1].point[3] [0 0]

# RIGHT WHEEL
polygon[2].points 4
polygon[2].point[0] [0 11]
polygon[2].point[1] [13 11]
polygon[2].point[2] [13 9]
polygon[2].point[3] [0 9]

#CAMERA
polygon[3].points 4
polygon[3].point[0] [12 7]
polygon[3].point[1] [14 7]
polygon[3].point[2] [14 4]
polygon[3].point[3] [12 4]


  # draw a nose on the robot so we can see which way it points
  gui_nose 1
  #mass 1
  #
  # differential steering model
  drive "diff"
)
