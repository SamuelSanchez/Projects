define map model
(
   # sombre, sensible, artistic
   color "black"
   # most maps will need a bounding box
   boundary 1   #gives a boundary
   gui_nose 0   #gives an arrow that displays the direction of the map
   gui_grid 1	#makes the grid for the map
   gui_movemask 0	# make it able to move robots 1: true
   gui_outline 0

   fiducial_return 0
   gripper_return 0
)
