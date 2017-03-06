/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.core.graphiti.dcd.ui.internal.diagram.patterns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;

import gov.redhawk.core.graphiti.dcd.ui.diagram.providers.DeviceManagerImageProvider;
import gov.redhawk.core.graphiti.dcd.ui.ext.RHDeviceGxFactory;
import gov.redhawk.core.graphiti.dcd.ui.ext.ServiceShape;
import gov.redhawk.core.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.core.graphiti.ui.util.DUtil;
import gov.redhawk.core.graphiti.ui.util.StyleUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class ServicePattern extends AbstractNodeComponentPattern implements IPattern {

	private static final EStructuralFeature[] SCD_PATH = new EStructuralFeature[] { PartitioningPackage.Literals.COMPONENT_INSTANTIATION__PLACEMENT,
		PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_FILE_REF, PartitioningPackage.Literals.COMPONENT_FILE_REF__FILE,
		PartitioningPackage.Literals.COMPONENT_FILE__SOFT_PKG, SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, SpdPackage.Literals.DESCRIPTOR__COMPONENT };

	public ServicePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return "Service";
	}

	@Override
	protected boolean isInstantiationApplicable(DcdComponentInstantiation instantiation) {
		SoftwareComponent scd = ScaEcoreUtils.getFeature(instantiation, SCD_PATH);

		if (scd == null) {
			return true;
		}

		switch (SoftwareComponent.Util.getWellKnownComponentType(scd)) {
		case SERVICE:
			return true;
		default:
			return false;
		}
	}

	@Override
	protected ServiceShape createContainerShape() {
		return RHDeviceGxFactory.eINSTANCE.createServiceShape();
	}

	/**
	 * Return all ServiceShape in Diagram (recursively)
	 * @param containerShape
	 * @return
	 */
	public static List<ServiceShape> getAllServiceShapes(ContainerShape containerShape) {
		List<ServiceShape> children = new ArrayList<ServiceShape>();
		if (containerShape instanceof ServiceShape) {
			children.add((ServiceShape) containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllServiceShapes((ContainerShape) s));
				}
			}
		}
		return children;
	}

	@Override
	protected String getOuterImageId() {
		return ImageProvider.IMG_SPD;
	}

	@Override
	protected String getInnerImageId() {
		return DeviceManagerImageProvider.IMG_SERVICE;
	}

	@Override
	protected String getStyleForOuter() {
		return StyleUtil.OUTER_SHAPE;
	}

	@Override
	protected String getStyleForInner() {
		return StyleUtil.COMPONENT_INNER;
	}

	/**
	 * Returns service, dcd, ports. Order does matter.
	 */
	protected List<EObject> getBusinessObjectsToLink(EObject componentInstantiation) {
		// get dcd from diagram, we need to link it to all shapes so the diagram will update when changes occur
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		// ORDER MATTERS, CI must be first
		businessObjectsToLink.add(componentInstantiation);
		businessObjectsToLink.add(dcd);

		return businessObjectsToLink;
	}
}
