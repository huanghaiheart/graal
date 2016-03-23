/*
 * Copyright (c) 2014, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.nodes.java;

import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.meta.ResolvedJavaType;
import jdk.vm.ci.meta.TriState;

import com.oracle.graal.compiler.common.type.Stamp;
import com.oracle.graal.graph.Node;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.graph.spi.Canonicalizable;
import com.oracle.graal.graph.spi.CanonicalizerTool;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.BinaryOpLogicNode;
import com.oracle.graal.nodes.LogicConstantNode;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.spi.Lowerable;
import com.oracle.graal.nodes.spi.LoweringTool;

/**
 * The {@code ClassIsAssignableFromNode} represents a type check against {@link Class} instead of
 * against instances. This is used, for instance, to intrinsify
 * {@link Class#isAssignableFrom(Class)} .
 */
@NodeInfo
public final class ClassIsAssignableFromNode extends BinaryOpLogicNode implements Canonicalizable.Binary<ValueNode>, Lowerable {

    public static final NodeClass<ClassIsAssignableFromNode> TYPE = NodeClass.create(ClassIsAssignableFromNode.class);

    public ClassIsAssignableFromNode(ValueNode thisClass, ValueNode otherClass) {
        super(TYPE, thisClass, otherClass);
    }

    public Object getThisClass() {
        return getX();
    }

    public Object getOtherClass() {
        return getY();
    }

    @Override
    public Node canonical(CanonicalizerTool tool, ValueNode forX, ValueNode forY) {
        if (forX.isConstant() && forY.isConstant()) {
            ConstantReflectionProvider constantReflection = tool.getConstantReflection();
            ResolvedJavaType thisType = constantReflection.asJavaType(forX.asJavaConstant());
            ResolvedJavaType otherType = constantReflection.asJavaType(forY.asJavaConstant());
            if (thisType != null && otherType != null) {
                return LogicConstantNode.forBoolean(thisType.isAssignableFrom(otherType));
            }
        }
        return this;
    }

    @Override
    public void lower(LoweringTool tool) {
        tool.getLowerer().lower(this, tool);
    }

    @Override
    public Stamp getSucceedingStampForX(boolean negated) {
        return null;
    }

    @Override
    public Stamp getSucceedingStampForY(boolean negated) {
        return null;
    }

    @Override
    public TriState tryFold(Stamp xStamp, Stamp yStamp) {
        return TriState.UNKNOWN;
    }

}
