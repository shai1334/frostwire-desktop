/*
 * Created on 19 Jun 2006
 * Created by Paul Gardner
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.plugins.messaging.generic;

import org.gudy.azureus2.plugins.messaging.MessageException;
import org.gudy.azureus2.plugins.network.RateLimiter;
import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public interface 
GenericMessageConnection 
{
	public static final int TT_NONE			= 0;
	public static final int TT_TCP			= 0;
	public static final int TT_UDP			= 0;
	public static final int TT_INDIRECT		= 0;
	
	public GenericMessageEndpoint
	getEndpoint();
	
	public void
	connect()
	
		throws MessageException;
	
	public void
	send(
		PooledByteBuffer			message )
	
		throws MessageException;
	
	public void
	close()
	
		throws MessageException;
	
	public int
	getMaximumMessageSize();
	
	public String
	getType();
	
	public int
	getTransportType();
	
	public void
	addInboundRateLimiter(
		RateLimiter		limiter );
	
	public void
	removeInboundRateLimiter(
		RateLimiter		limiter );

	public void
	addOutboundRateLimiter(
		RateLimiter		limiter );
	
	public void
	removeOutboundRateLimiter(
		RateLimiter		limiter );
	
	public void
	addListener(
		GenericMessageConnectionListener		listener );
	
	public void
	removeListener(
		GenericMessageConnectionListener		listener );
}
