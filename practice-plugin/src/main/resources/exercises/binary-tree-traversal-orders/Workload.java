package com.jinloes.practice;
final class Workload { static final String UNIT="node"; static final int[] SIZES={4095,8191,16383,32767,65535}; private Workload(){} static Object prepare(int size){ Solution.TreeNode[] a=new Solution.TreeNode[size]; for(int i=0;i<size;i++)a[i]=new Solution.TreeNode(i); for(int i=0;i<size;i++){ if(2*i+1<size)a[i].left=a[2*i+1]; if(2*i+2<size)a[i].right=a[2*i+2]; } return a[0]; } static void run(Object state){ if(Solution.traversalOrders((Solution.TreeNode)state).preorder().isEmpty())throw new IllegalStateException(); } static int units(int size){ return 1; } }

