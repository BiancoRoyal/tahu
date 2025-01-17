/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2022 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package org.eclipse.tahu.edge.sim;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.tahu.message.model.DeviceDescriptor;
import org.eclipse.tahu.message.model.EdgeNodeDescriptor;
import org.eclipse.tahu.message.model.Metric;
import org.eclipse.tahu.message.model.Metric.MetricBuilder;
import org.eclipse.tahu.message.model.MetricDataType;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.eclipse.tahu.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import org.eclipse.tahu.message.model.SparkplugBPayloadMap;
import org.eclipse.tahu.message.model.SparkplugBPayloadMap.SparkplugBPayloadMapBuilder;
import org.eclipse.tahu.message.model.SparkplugDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomDataSimulator implements DataSimulator {

	private static Logger logger = LoggerFactory.getLogger(RandomDataSimulator.class.getName());

	private final int numNodeMetrics;
	private final Map<SparkplugDescriptor, Integer> numDeviceMetrics;

	private final Random random = new Random();
	private final Map<SparkplugDescriptor, Map<String, Metric>> metricMaps = new HashMap<>();
	private final Map<SparkplugDescriptor, Long> lastUpdateMap = new HashMap<>();

	public RandomDataSimulator(int numNodeMetrics, Map<SparkplugDescriptor, Integer> numDeviceMetrics) {
		this.numNodeMetrics = numNodeMetrics;
		this.numDeviceMetrics = numDeviceMetrics;
	}

	// DataSimultor API
	@Override
	public SparkplugBPayloadMap getNodeBirthPayload(EdgeNodeDescriptor edgeNodeDescriptor) {
		try {
			Date now = new Date();
			Map<String, Metric> metricMap = new HashMap<>();

			SparkplugBPayloadMapBuilder payloadBuilder = new SparkplugBPayloadMapBuilder();
			payloadBuilder.setTimestamp(now);
			for (int i = 0; i < numNodeMetrics; i++) {
				Metric metric = getRandomMetric("NT", i, true);
				metricMap.put(metric.getName(), metric);
				payloadBuilder.addMetric(metric);
			}

			metricMaps.put(edgeNodeDescriptor, metricMap);
			lastUpdateMap.put(edgeNodeDescriptor, now.getTime());
			return payloadBuilder.createPayload();
		} catch (Exception e) {
			logger.error("Failed to get the NBIRTH", e);
			return null;
		}
	}

	// DataSimultor API
	@Override
	public SparkplugBPayload getDeviceBirthPayload(DeviceDescriptor deviceDescriptor) {
		try {
			Date now = new Date();
			Map<String, Metric> metricMap = new HashMap<>();

			SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder();
			payloadBuilder.setTimestamp(now);
			logger.info("Getting number of metrics for {}", deviceDescriptor);
			for (int i = 0; i < numDeviceMetrics.get(deviceDescriptor); i++) {
				Metric metric = getRandomMetric("DT", i, true);
				metricMap.put(metric.getName(), metric);
				payloadBuilder.addMetric(metric);
			}

			metricMaps.put(deviceDescriptor, metricMap);
			lastUpdateMap.put(deviceDescriptor, now.getTime());
			return payloadBuilder.createPayload();
		} catch (Exception e) {
			logger.error("Failed to get the DBIRTH", e);
			return null;
		}
	}

	// DataSimultor API
	@Override
	public SparkplugBPayload getDeviceDataPayload(DeviceDescriptor deviceDescriptor) {
		try {
			Date now = new Date();
			Map<String, Metric> metricMap = new HashMap<>();

			SparkplugBPayloadBuilder payloadBuilder = new SparkplugBPayloadBuilder();
			payloadBuilder.setTimestamp(now);
			logger.info("Getting number of metrics for {}", deviceDescriptor);
			for (int i = 0; i < numDeviceMetrics.get(deviceDescriptor); i++) {
				Metric metric = getRandomMetric("DT", i, true);
				metricMap.put(metric.getName(), metric);
				payloadBuilder.addMetric(metric);
			}

			metricMaps.put(deviceDescriptor, metricMap);
			lastUpdateMap.put(deviceDescriptor, now.getTime());
			return payloadBuilder.createPayload();
		} catch (Exception e) {
			logger.error("Failed to get the DBIRTH", e);
			return null;
		}
	}

	// DataSimultor API
	@Override
	public boolean hasMetric(SparkplugDescriptor sparkplugDescriptor, String metricName) {
		if (metricMaps.containsKey(sparkplugDescriptor)
				&& metricMaps.get(sparkplugDescriptor).get(metricName) != null) {
			return true;
		} else {
			return false;
		}
	}

	// DataSimultor API
	@Override
	public Metric handleMetricWrite(SparkplugDescriptor sparkplugDescriptor, Metric metric) {
		// No-op for this simulator - just return the metric as though the value was 'written'
		return metric;
	}

	private Metric getRandomMetric(String namePrefix, int index, boolean isBirth) throws Exception {
		int remainder = index % 13;
		switch (remainder) {
			case 0:
				byte[] bytes = new byte[1];
				random.nextBytes(bytes);
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Int8, bytes[0]).createMetric();
			case 1:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Int16,
						(short) random.nextInt(Short.MAX_VALUE + 1)).createMetric();
			case 2:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Int32, random.nextInt())
						.createMetric();
			case 3:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Int64, random.nextLong())
						.createMetric();
			case 4:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.UInt8,
						(short) random.nextInt(Short.MAX_VALUE + 1)).createMetric();
			case 5:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.UInt16, random.nextInt())
						.createMetric();
			case 6:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.UInt32, random.nextLong())
						.createMetric();
			case 7:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.UInt64, new BigInteger(64, random))
						.createMetric();
			case 8:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Float, random.nextFloat())
						.createMetric();
			case 9:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Double, random.nextDouble())
						.createMetric();
			case 10:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.String,
						RandomStringUtils.randomAlphanumeric(8).toUpperCase()).createMetric();
			case 11:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.DateTime, new Date(random.nextLong()))
						.createMetric();
			case 12:
				return new MetricBuilder(namePrefix + "-" + index, MetricDataType.Text,
						RandomStringUtils.randomAlphanumeric(8).toUpperCase()).createMetric();
			default:
				logger.error("Failed to get a metric for index {}", index);
				return null;
		}
	}
}
