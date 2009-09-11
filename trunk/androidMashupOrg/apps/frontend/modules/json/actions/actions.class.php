<?php

/**
 * json actions.
 *
 * @package    mashup
 * @subpackage json
 * @author     Your name here
 * @version    SVN: $Id: actions.class.php 12479 2008-10-31 10:54:40Z fabien $
 */
class jsonActions extends sfActions
{
	/**
	 * Executes index action
	 *
	 * @param sfRequest $request A request object
	 */
	public function executeIndex(sfWebRequest $request) {
		$json = "";
		
		$applications = ApplicationPeer::doSelect ( new Criteria ( ) );
		
//		echo "<pre>";
//			print_r($applications);
//			die();
			$json .= json_encode($applications, JSON_FORCE_OBJECT);	
		
//		foreach ( $applications as $application ) {
//			
//			
//			$intents = $application->getIntents();
//			foreach ( $intents as $intent ) {
//			
//				$categories = $intent->getCategories();
//				foreach ( $categories as $category ) {
//			
//				}
//				
//				$extras = $intent->getExtras ();
//				foreach ( $extras as $extra ) {
//				}
//			}
//		}
		
		//		$xml = htmlspecialchars ( $xml );
		//		echo "<pre>";
		//		echo $xml;
		//		die ();
		$this->json = $json;
		$response = $this->getResponse();
		$response->setHttpHeader('Content-type','text/html', true);
		$response->setHttpHeader('charset','utf-8', true);
		
	}
}
