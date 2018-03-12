/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.plcore.value;


/**
 * TODO Write a reasonably detailed description of the purpose and use of the class here.
 * @param <T> 
 * 
 */
public interface IValueRange<T> {

    /**
     * Tests whether this range contains a value or not.
     * 
     * @param value -- the value being tested
     * @return true if this range contains the parameter value, or false otherwise
     */
    public boolean contains(T value);

    /**
     * Reports where a value occurs within this range.
     * <ul>
     * <li>0 is returned if the value is within the range. That is, the value is greater than or
     * equal to the start value and less than the end value.</li>
     * <li>+1 is returned if the value is higher than the range. That is, the value is greater than
     * or equal to the end value.</li>
     * <li>-1 is returned if the value is lower than the range. That is, the value is less than the
     * start value.</li>
     * </ul>
     * 
     * @param value
     * @return the value 0 if value is within the range, +1 if it is greater than the range, and
     * -1 if it is less than the range.
     */
    public int compareToRange(T value);

    
    public T getBeginValue ();
    
    
    public T getEndValue ();
    
}
