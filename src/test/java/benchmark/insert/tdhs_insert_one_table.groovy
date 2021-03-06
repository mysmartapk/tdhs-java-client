/*
 * Copyright(C) 2011-2012 Alibaba Group Holding Limited
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  Authors:
 *    wentong <wentong@taobao.com>
 */

package benchmark.insert

import benchmark.StressTest
import com.taobao.tdhs.client.TDHSClient
import com.taobao.tdhs.client.TDHSClientImpl
import com.taobao.tdhs.client.response.TDHSResponse
import com.taobao.tdhs.client.response.TDHSResponseEnum
import java.util.concurrent.atomic.AtomicLong
import benchmark.PropUtil

/**
 * @author <a href="mailto:wentong@taobao.com">文通</a>
 * @since 12-2-9 下午3:31
 *
 */

int cur = 16
AtomicLong id = new AtomicLong(0)
TDHSClient client = new TDHSClientImpl(new InetSocketAddress(PropUtil.host, 9999), 2);

def s = new StressTest()
s.add({
  def _id = id.incrementAndGet()
  int end = it % cur
  String table = "test_" + end.toString().padLeft(4, "0");
  TDHSResponse response = client.createStatement(end).insert().use("benchmark_insert").from(table)
          .value("k", "10000")
          .value("i", _id.toString())
          .value("c", _id.toString() + "_abcdefghijklmnopqrstuvwxyz").insert()

  if (response != null && response.getStatus() != TDHSResponseEnum.ClientStatus.OK) {
    System.out.println(response);
  }
}, 100)

s.run()
client.shutdown()
