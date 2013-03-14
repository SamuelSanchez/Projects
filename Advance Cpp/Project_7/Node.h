#ifndef NODE_H
#define NODE_H

template <class K,typename V>
class Node
{
   public:
	  K key;
	  V value;
	  Node * next;

      //Default, Copy and Assignment constructor
      Node();
      Node(const K&, const V&);
      Node(const Node&);
      Node& operator= (const Node&);
      ~Node();
	  //Comparison operators
	  bool operator> (const Node&) const;
	  bool operator< (const Node&) const;
	  bool operator>= (const Node&) const;
	  bool operator<= (const Node&) const;
	  bool operator== (const Node&) const;
	  bool operator!= (const Node&) const;
	  
	  template <class _K,class _V>
	  friend std::ostream& operator<< (std::ostream&, const Node<_K,_V>&);
};
#endif /* NODE_H */

/* Node.cpp */
template <class K,typename V>
Node<K,V>::Node():next(NULL)
{
    #ifdef DEBUG
		std::cout << "Node : Empty Constructor" << std::endl;
    #endif
}

template <class K,typename V>
Node<K,V>::Node(const K& key, const V& value):key(),value(), next(NULL)
{
    #ifdef DEBUG
		std::cout << "Node : Constructor " << std::endl;
    #endif
    this->key = key;
    this->value = value;
}

template <class K,typename V>
Node<K,V>::Node(const Node<K,V>& node)
{
    #ifdef DEBUG
		std::cout << "Node : Copy Constructor " << std::endl;
    #endif
    key   = node.key;
    value = node.value;
    next  = node.next;
}

template <class K,typename V>
Node<K,V>& Node<K,V>::operator= (const Node<K,V>& node)
{
    #ifdef DEBUG
		std::cout << "Node : Operator= " << std::endl;
    #endif
    //Check if it's the same object - return
    if(this != &node){
        //Assign values
        key   = node.key;
		value = node.value;
		next  = node.next;
    }
    return *this;
}

template <class K,typename V>
Node<K,V>::~Node()
{
   #ifdef DEBUG
      std::cout << "Node : ~Destructor " << std::endl;
   #endif
   next = NULL;
}

//Comparison operators
template <class K,typename V>
bool Node<K,V>::operator> (const Node<K,V>& node) const
{
	return key > node.key;
}

template <class K,typename V>
bool Node<K,V>::operator< (const Node<K,V>& node) const
{
	return key < node.key;
}

template <class K,typename V>
bool Node<K,V>::operator>= (const Node<K,V>& node) const
{
	return key >= node.key;
}

template <class K,typename V>
bool Node<K,V>::operator<= (const Node<K,V>& node) const
{
	return key <= node.key;
}

template <class K,typename V>
bool Node<K,V>::operator== (const Node<K,V>& node) const
{
	return key == node.key;
}

template <class K,typename V>
bool Node<K,V>::operator!= (const Node<K,V>& node) const
{
	return key != node.key;
}

template <class K,typename V>
std::ostream& operator<< (std::ostream& os, const Node<K,V>& node)
{
   os << node.value << " ";
   return os;
}