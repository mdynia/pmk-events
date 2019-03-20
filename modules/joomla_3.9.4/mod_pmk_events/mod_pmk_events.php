<?php
	// No direct access
	defined('_JEXEC') or die;

	require_once dirname(__FILE__) . '/helper.php';
	
	$eventList = new PmkEventList($params);	
	$eventList->query();

	require_once JModuleHelper::getLayoutPath('mod_pmk_events');
?>