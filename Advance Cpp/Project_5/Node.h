#ifndef NODE_H
#define NODE_H

class Node
{
   public:
      int coefficient;
      int exponent;
	  Node * next;

      //Default, Copy and Assignment constructor
      Node();
      Node(const int&, const int&);
      Node(const Node&);
      Node& operator= (const Node&);
      ~Node();

	  friend std::ostream& operator<< (std::ostream&, const Node&);
	  std::string toString() const;
};
#endif /* NODE_H */

/* Node.cpp */
Node::Node():coefficient(0), exponent(0), next(NULL)
{
    #ifdef DEBUG
    std::cout << "Node : Empty Constructor" << std::endl;
    #endif
}

Node::Node(const int& coefficient, const int& exponent): next(NULL)
{
    #ifdef DEBUG
    std::cout << "Node : Constructor : [ " << coefficient << " - " << exponent  << " ]"<< std::endl;
    #endif
    this->coefficient = coefficient;
    this->exponent = exponent;
}

Node::Node(const Node& node)
{
    #ifdef DEBUG
    std::cout << "Node : Copy Constructor [ " << node.coefficient << " - " << node.exponent  << " ]"<< std::endl;
    #endif
    coefficient = node.coefficient;
    exponent    = node.exponent;
    next        = node.next;
}

Node& Node::operator= (const Node& node)
{
    #ifdef DEBUG
    std::cout << "Node : Operator= [ " << node.coefficient << " - " << node.exponent  << " ]"<< std::endl;
    #endif
    //Check if it's the same object - return
    if(this != &node){
        //Assign values
        coefficient = node.coefficient;
        exponent    = node.exponent;
        next        = node.next;
    }
    return *this;
}

Node::~Node()
{
   #ifdef DEBUG
      std::cout << "Node : ~Destructor " << coefficient << " " << exponent << std::endl;
   #endif
   next = NULL;
}

std::ostream& operator<< (std::ostream& os, const Node& node)
{
   os << node.coefficient << " " << node.exponent << " ";
   return os;
}

std::string Node::toString() const
{
    std::stringstream value;
	value << coefficient << " " << exponent << " ";
	return value.str();
}
