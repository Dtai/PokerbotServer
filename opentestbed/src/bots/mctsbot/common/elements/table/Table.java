/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package bots.mctsbot.common.elements.table;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Table implements Serializable, Comparable<Table> {

	private static final long serialVersionUID = 527893735230918726L;

	@XmlAttribute
	protected TableId id;

	@XmlAttribute
	protected String name;

	public Table(TableId id, String name) {
		this.id = id;
		this.name = name;
	}

	public Table() {
		// no op
	}

	/**
	 * The id of this table.
	 * 
	 * This id is unique.
	 * 
	 * @return The id of this table.
	 */
	public TableId getId() {
		return id;
	}

	/**
	 * Returns the name of this table.
	 * 
	 * The name is not guaranteed unique.
	 * 
	 * @return The name of this table.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName() + " (" + getId() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Table))
			return false;
		Table other = (Table) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public int compareTo(Table o) {
		long diff = id.getId() - o.id.getId();
		if (diff > 0)
			return 1;
		if (diff < 0)
			return -1;
		return 0;
	}
}
