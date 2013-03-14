#ifndef VNT_H
#define VNT_H

#include "Matrix.h"

//TODO: This Class is not complete
class VNT{
	private:
		struct val{
			int value, loc;
			val():value(0),loc(-1){};
		};
		Matrix<int> a;
		val * rowsPosition;//Store the position of the max value of the row for each column
		int row;
	public:
		VNT(){
			rowsPosition = 0;
			row = 0;
		}
		
		VNT(int _rows, int _cols){
			a = Matrix<int>(_rows,_cols);
			rowsPosition = new val[_cols];
			row = _rows;
			for(int i = 0; i < _cols; i++){
				rowsPosition[i].value = -1;
			}
			a.populate(INT_MAX);
		}
		
		~VNT(){
			delete[] rowsPosition;
		}
		
		//TODO: This method needs some ajustments
		bool add(int number){
			bool temp = false;
			for(int i = 0; i < a.getColumnCount(); i++){
				if(rowsPosition[i].loc > row){
					temp = false;
					break;
				}
				if(rowsPosition[i].value < number){
					rowsPosition[i].value = number;
					rowsPosition[i].loc++;
					a[i][rowsPosition[i].loc] = number;
					temp = true;
					break;
				}
			}
			//No data was alocated
			return temp;
		}
		
		int getMin(){
			int n = -1;
			if(a.getRowCount() > 0 && a.getColumnCount() > 0){
				n = a[0][0];
				a[0][0] = INT_MAX;
			}
			return n;
		}
		
		bool find(int num){
			if(a.getRowCount() == 0 || a.getColumnCount()== 0){
				return false;
			}
			int low = a.getLow();
			int high = a.getHigh();
			int _low = a[0].getLow();
			int _high = a[0].getHigh();
			for(int i = low; i <= high; i++){
				for(int j = _low; j < _high; j++){
					if(a[i][j] == num){
						return true;
					}
				}
			}
			return false;
		}
		
		friend std::ostream& operator<< (std::ostream&, const VNT&);
};
#endif /* VNT_H */

std::ostream& operator<< (std::ostream& os, const VNT& vnt){
	os << vnt.a << std::endl;
	return os;
}