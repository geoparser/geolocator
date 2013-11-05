/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.resource.trie;

/**
 * The trie tree has only two child node for each node. right is the brotherhood, down is the child-parent hood.
 * We use the linked list for the brother hood, in order to save storing space. 
 * Generally speaking, this is the binary tree, the only difference to the traditional tree is that,
 * one node pointer points to a son, the other points to the brother.
 * @author Wei Zhang
 *
 */
public class Node {

	private char c;
	public Node right, down;
	private long[] ids;// not an location ending, if id is empty.
	
	//sometimes we want to check the trie tree for authentication of the string(it's original form). 
	//for doing this, we store another original string field into the tree at each leaf node.
	//so we don't need to go to the index to look it up.
	
	private String[] OriginalString;//This is for storing original string for checking accent match.
	
	public Node(char c2) {
		// TODO Auto-generated constructor stub
		this.c = c2;
	}

	public boolean containsChild(char c2) {
		// TODO Auto-generated method stub

		if (this.down == null)
			return false;

		Node cur = this.down;
		while (cur != null) {
			if (cur.c == c2)
				return true;
			cur = cur.right;
		}
		return false;
	}

	public void addChildren(Node child) {
		// TODO Auto-generated method stub

		if (this.down == null)
			this.down = (Node) child;
		else {
			Node cur = this.down;
			if (cur.right == null)
				cur.right = (Node) child;
			else {
				Node temp = cur.right;
				cur.right = (Node) child;
				((Node) child).right = temp;
			}
		}
	}

	public Node getChild(char c2) {
		// TODO Auto-generated method stub
		if (this.down == null)
			return null;
		Node cur = this.down;
		while (cur != null) {
			if (cur.c == c2)
				return cur;
			cur = cur.right;
		}
		return null;
	}

	
	public void addValue( long id) {
		// TODO Auto-generated method stub
		if (this.ids == null) {
			this.ids = new long[1];
			this.ids[0] = id;
			//add string
		} else {
			int len = this.ids.length;
			long[] temp = new long[len + 1];
			for (int j = 0; j < len; j++)
				temp[j] = this.ids[j];
			temp[len] = id;
			this.ids = temp;
			temp=null;
		}
	}
	
	//this is the id and string value version for add.
	public void addValue( long id, String s) {
		// TODO Auto-generated method stub
		if (this.ids == null) {
			this.ids = new long[1];
			this.ids[0] = id;
			//add string
			this.OriginalString=new String[1];
			this.OriginalString[0]=s;
		} else {
			int len = this.ids.length;
			long[] tempint = new long[len + 1];
			String[] tempstring=new String[len+1];
			for (int j = 0; j < len; j++)
			{	
				tempint[j] = this.ids[j];
				tempstring[j] = this.OriginalString[j];
			}
			tempint[len] = id;
			tempstring[len] =s;
			this.ids = tempint;
			this.OriginalString=tempstring;
			tempint=null;tempstring=null;
		}
	}
	public boolean isChildrenEmpty() {
		// TODO Auto-generated method stub

		if (this.down == null)
			return true;
		else
			return false;
	}

	public boolean isLocation() {
		if (this.ids == null)
			return false;
		else
			return true;
	}

	public long[] getIDValue() {
		return this.ids;
	}
	
	public String[] getStringValue(){
		return this.OriginalString;
	}
	
}
