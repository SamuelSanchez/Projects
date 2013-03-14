#ifndef ROBOTSPROP_H
#define ROBOTSPROP_H

#include "Point.h"

struct robotsProp {
    long id;
    double  x;
    double  y;
    double theta;
    bool robotFound;
//    vector<Point> target_pool;
};
#endif  /* ROBOTSPROP_H */

