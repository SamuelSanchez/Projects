#ifndef SA_H
#define SA_H

template <class T>
class SA{
	private:
		int low, high;	//This are indexes so they must be integers
		T * p;			//This is the storage so it must be a template
	public:
		SA(){
			#ifdef DEBUG
				std::cout << "SA : Empty Constructor" << std::endl;
			#endif
			low = 0;
			high = -1;
			p = NULL;
		}
		
		SA(int i){
			#ifdef DEBUG
				std::cout << "SA : Constructor: High [ " << i << " ]" << std::endl;
			#endif
			if( i < 0 ){
				std::cout<< "constructor error in bounds definition" << std::endl;
				exit(1);
			}
			
			low = 0;
			high = i - 1;
			p = new T[i];
		}
		
		SA(int l, int h){
			#ifdef DEBUG
				std::cout << "SA : Constructor: Low [ " << l 
				<< " ] - high [ " << h << " ]" << std::endl;
			#endif
			if( ( h-l+1 ) <= 0 ){
				std::cout<< "constructor error in bounds definition" << std::endl;
				exit(1);
			}
			if( h < l ){
				std::cout<< "Max cannot be less than Min" << std::endl;
				exit(1);
			}
			low = l;
			high = h;
			p = new T[h-l+1];
		}
		
		SA(const SA & s){
			#ifdef DEBUG
				std::cout << "SA : Copy Constructor" << std::endl;
			#endif
			int size = s.high - s.low + 1;
			p = new T[size];
			for(int i=0; i<size; i++){
				p[i]=s.p[i];
			}
			low = s.low;
			high = s.high;
		}
		
		~SA(){
			#ifdef DEBUG
				std::cout << "SA : Destructor" << std::endl;
			#endif
			delete [] p;
		}
		
		T& operator[] (int i){
			#ifdef DEBUG
				std::cout << "SA : Operator[ " << i << " ]" << std::endl;
			#endif
			int size = high - low + 1;
			if( size < 1){ //No space was alocated at all
				std::cout << "no data has been alocated!" << std::endl;
				exit(1);
			}
			if( i<low || i>high ){
				std::cout<< "index " << i << " out of range - high [ " <<
				high << " ] - low [ " << low << " ]" << std::endl;
				exit(1);
			}
			return p[i-low];
		}
		
		const T& operator[] (int i) const{
			#ifdef DEBUG
				std::cout << "SA : const Operator[ " << i << " ]" << std::endl;
			#endif
			int size = high - low + 1;
			if( size < 1){ //No space was alocated at all
				std::cout << "no data has been alocated!" << std::endl;
				exit(1);
			}
			if( i<low || i>high ){
				std::cout<< "index " << i << " out of range - high [ " << 
				high << " ] - low [ " << low << " ]" << std::endl;
				exit(1);
			}
			return p[i-low];
		}
		
		SA& operator= (const SA & s){
			#ifdef DEBUG
				std::cout << "SA : Assignment Operator" << std::endl;
			#endif
			if( this == &s )
				return *this;
			delete [] p;
			int size = s.high - s.low + 1;
			p = new T[size];
			for(int i=0; i<size; i++){
				p[i]=s.p[i];
			}
			low = s.low;
			high = s.high;
			return *this;
		}
		
		T* operator+(int i){
			#ifdef DEBUG
				std::cout << "SA : Pointer Addition" << std::endl;
			#endif
			//int size = high - low + 1;
			//if( size < 1){ //No space was alocated at all
			//	std::cout << "no data has been alocated!" << std::endl;
			//	exit(1);
			//}
			if( i < low || i > high ){
				std::cout << "index " << i << " out of range" << std::endl;
				exit(1);
			}
			return &p[i-low];
		}
		
		const T* operator+(int i) const{
			#ifdef DEBUG
				std::cout << "SA : Const Pointer Addition" << std::endl;
			#endif
			//int size = high - low + 1;
			//if( size < 1){ //No space was alocated at all
			//	std::cout << "no data has been alocated!" << std::endl;
			//	exit(1);
			//}
			if( i < low || i > high ){
				std::cout << "index " << i << " out of range" << std::endl;
				exit(1);
			}
			return &p[i-low];
		}
		
		T& operator*(){
			#ifdef DEBUG
				std::cout << "SA : Pointer Dereference" << std::endl;
			#endif
			int size = high - low + 1;
			if( size < 1){ //No space was alocated at all
				std::cout << "no data to be dereference" << std::endl;
				exit(1);
			}
			return *p[low];
		}
		
		const T& operator*() const{
			#ifdef DEBUG
				std::cout << "SA : Const Pointer Dereference" << std::endl;
			#endif
			int size = high - low + 1;
			if( size < 1){ //No space was alocated at all
				std::cout << "no data to be dereference" << std::endl;
				exit(1);
			}
			return *p[low];
		}
		
		int getHigh() const{
			return high;
		}
		
		int getLow() const{
			return low;
		}
		
		template <class U>
		friend std::ostream& operator<<(std::ostream&, const SA<U>&);
};
#endif /* SA_H */
	
template <class T>
std::ostream& operator<<(std::ostream& os, const SA<T>& s){
	for(int i=s.low; i<=s.high; i++){
		os << s[i] << "\t";
	}
	return os;
}