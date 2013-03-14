#ifndef MATRIX_H
#define MATRIX_H
#include "SA.h"

template <class T>
class Matrix{
	private:
		SA<SA<T> > matrix;
		int low, high;
	public:
		Matrix(){
			#ifdef DEBUG
				std::cout << "MATRIX : Empty Constructor" << std::endl;
			#endif
			low = 0;
			high = -1;
			//matrix = SA<SA<T> >(0);
		}
		
		Matrix(int rows, int columns){
			#ifdef DEBUG
				std::cout << "MATRIX : Constructor : rows [ " << rows
				<< " ] - cols [ " << columns << " ]" << std::endl;
			#endif
			if(rows < 0 || columns < 0){				
				std::cout<< "constructor error in bounds definition" << std::endl;
				exit(1);
			}
			
			low = 0;
			high = rows-1;
			matrix = SA<SA<T> >(rows);
			for(int i = 0; i < rows; i++){
				#ifdef DEBUG
					std::cout << "MATRIX : i ( " << i << " )" << std::endl;
				#endif
				matrix[i] = SA<T>(columns);
			}
		}
		
		Matrix(int lRow, int uRow, int lCol, int uCol){
			#ifdef DEBUG
				std::cout << "MATRIX : Constructor : lRow [ " << lRow
				<< " ] - uRow [ " << uRow << " ] - lCol [ "
				<< lCol << " ] - uCol [ " << uCol << " ]" << std::endl;
			#endif
			if( ( uRow-lRow+1 ) <= 0  || ( uCol-lCol+1 ) <= 0 ){
				std::cout<< "constructor error in bounds definition" << std::endl;
				exit(1);
			}
			if( (uRow < lRow) || (uCol < lCol) ){
				std::cout<< "Max cannot be less than Min" << std::endl;
				exit(1);
			}
			
			low = lRow;
			high = uRow;
			matrix = SA<SA<T> >(lRow,uRow);
			for(int i = lRow; i <= uRow; i++){
				matrix[i] = SA<T>(lCol,uCol);
			}
		}
		
		Matrix(const Matrix & m){
			#ifdef DEBUG
				std::cout << "MATRIX : Copy Constructor" << std::endl;
			#endif
			matrix = m.matrix;
			low = m.low;
			high = m.high;
		}
		
		~Matrix(){
			#ifdef DEBUG
				std::cout << "MATRIX : Destructor" << std::endl;
			#endif
		}
		
		SA<T>& operator[] (int i){
			#ifdef DEBUG
				std::cout << "MATRIX : Operator[ " << i << " ]" << std::endl;
			#endif
			if( i<low || i>high ){
				std::cout<< "index " << i << " out of range" << std::endl;
				exit(1);
			}
			return matrix[i];
		}
		
		const SA<T>& operator[] (int i) const{
			#ifdef DEBUG
				std::cout << "MATRIX : Operator[ " << i << " ]" << std::endl;
			#endif
			if( i<low || i>high ){
				std::cout<< "index " << i << " out of range" << std::endl;
				exit(1);
			}
			return matrix[i];
		}
		
		Matrix& operator= (const Matrix & m){
			#ifdef DEBUG
				std::cout << "MATRIX : Assignment Operator" << std::endl;
			#endif
			if( this == &m )
				return *this;
			matrix = m.matrix;
			low = m.low;
			high = m.high;
			return *this;
		}
		
		SA<T>* operator+(int i){
			#ifdef DEBUG
				std::cout << "MATRIX : Pointer Addition [" << i << "]" << std::endl;
			#endif
			if( i < low || i > high ){
				std::cout << "index " << i << " out of range" << std::endl;
				exit(1);
			}
			return &matrix[i];
		}
		
		const SA<T>* operator+(int i) const{
			#ifdef DEBUG
				std::cout << "MATRIX : Const Pointer Addition [" << i << "]" << std::endl;
			#endif
			if( i < low || i > high ){
				std::cout << "index " << i << " out of range" << std::endl;
				exit(1);
			}
			return &matrix[i];
		}
		
		SA<T>& operator*(){
			#ifdef DEBUG
				std::cout << "MATRIX : Pointer Dereference" << std::endl;
			#endif
			int size = high - low + 1;
			if( size < 1){ //No space was alocated at all
				std::cout << "No data to be dereference" << std::endl;
				exit(1);
			}
			return *matrix[low];
		}
		
		const SA<T>& operator*() const{
			#ifdef DEBUG
				std::cout << "MATRIX : Const Pointer Dereference" << std::endl;
			#endif
			int size = high - low + 1;
			if( size < 1){ //No space was alocated at all
				std::cout << "No data to be dereference" << std::endl;
				exit(1);
			}
			return *matrix[low];
		}
		
		Matrix operator* (const Matrix& m){
			#ifdef DEBUG
				std::cout << "MATRIX : Multiplication" << std::endl;
			#endif
			//Check if there are rows
			int rows = high - low + 1;
			int mRows = m.high - m.low + 1;
			
			if( rows < 1 || mRows < 1 ){ //No space was alocated at all
				std::cout << "No rows were alocated" << std::endl;
				exit(1);
			}
			//Check if there are columns
			int _Low = matrix[low].getLow();
			int _MLow = m.matrix[m.getLow()].getLow();
			int cols = matrix[low].getHigh() - _Low + 1;
			int mCols = m.matrix[m.getLow()].getHigh() - _MLow + 1;

			if( cols < 1 || mCols < 1 ){ //No space was alocated at all
				std::cout << "No columns were alocated" << std::endl;
				exit(1);
			}
			//Check if they can be multiplied : (NxM)*(MxP) = NxP
			if( cols != mRows ){
				std::cout << "Matrix can not be multiplied : [" << rows << "][" <<
				cols << "] x [" << mRows << "][" << mCols << "]" << std::endl;
				exit(1);
			}
			//Multiply their values
			Matrix temp(rows,mCols);
			int value;
			
			for(int r = 0; r < rows; r++){
				for(int c = 0; c < mCols; c++){
					value = 0;
					for(int k = 0; k < mRows; k++){
						value += (matrix[r+low][k+_Low] * m.matrix[k+m.low][c+_MLow]);
					}
					temp[r][c] = value;
				}
			}
			return temp;
		}
		
		int getHigh() const{
			return high;
		}
		
		int getLow() const{
			return low;
		}
		
		int getRowCount() const{
			int size = high - low + 1;
			return size;
		}
		
		int getColumnCount() const{
			int size = high - low + 1;
			if( size < 1 ){ //Return size to prevent errors when getting data from it
				return size;
			}
			int cSize = matrix[low].getHigh() - matrix[low].getLow() + 1;
			if( cSize < 1 ){
				return size;
			}
			return cSize;
		}

		//Populate with random variables
		void populateRandom(int max = 10){
			srand( time(NULL) );
			for(int i = low; i <= high; i++){
				for(int j = matrix[low].getLow(); j <= matrix[low].getHigh(); j++){
					*(*(matrix + i) + j) = rand()%max + 0;
				}
			}
		}
		
		//Populate with random variables
		void populate(int max = -1){
			srand( time(NULL) );
			for(int i = low; i <= high; i++){
				for(int j = matrix[low].getLow(); j <= matrix[low].getHigh(); j++){
					*(*(matrix + i) + j) = max;
				}
			}
		}
		
		template <class U>
		friend std::ostream& operator<<(std::ostream&, const Matrix<U>&);
};
#endif /* MATRIX_H */

template <class T>
std::ostream& operator<<(std::ostream& os, const Matrix<T>& m){
	for(int i = m.low; i <= m.high; i++){
		os << m[i] << std::endl;
	}
	return os;
}